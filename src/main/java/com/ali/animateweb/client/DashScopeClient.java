package com.ali.animateweb.client;

import com.ali.animateweb.dto.DetectResponseDTO;
import com.ali.animateweb.dto.SubmitTaskResponseDTO;
import com.ali.animateweb.dto.TaskStatusResponseDTO;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class DashScopeClient {

    @Value("${dashscope.api-key}")
    private String apiKey;

    private static final String BASE = "https://dashscope.aliyuncs.com";
    private static final String UPLOAD_POLICY_URL = BASE + "/api/v1/uploads";
    private static final String DETECT_URL = BASE + "/api/v1/services/aigc/image2video/aa-detect";
    private static final String VIDEO_SYNTHESIS_URL = BASE + "/api/v1/services/aigc/image2video/video-synthesis/";
    private static final String TASKS_URL = BASE + "/api/v1/tasks/";

    /**
     * Step C
     * Upload pics to  DashScope OSS，then return oss://dashscope-instant/...
     */
    public String uploadToDashScopeOss(byte[] fileBytes, String filename, String modelName) {
        try (CloseableHttpClient http = HttpClients.createDefault()) {

            /* ---------- 1) get upload policy ---------- */
            String policyUrl = UPLOAD_POLICY_URL + "?action=getPolicy&model=" + modelName;
            HttpGet get = new HttpGet(policyUrl);
            get.addHeader("Authorization", "Bearer " + apiKey);
            get.addHeader("Content-Type", "application/json");

            String policyResp;
            try (CloseableHttpResponse resp = http.execute(get)) {
                policyResp = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
                if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new RuntimeException("getPolicy failed: " + policyResp);
                }
            }

            JSONObject policyJson = new JSONObject(policyResp);
            JSONObject data = policyJson.getJSONObject("data");

            /* ---------- 2) multipart upload ---------- */
            String uploadHost = data.getString("upload_host");
            String uploadDir = data.getString("upload_dir");
            String key = uploadDir + "/" + filename;

            HttpPost post = new HttpPost(uploadHost);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            builder.addTextBody("OSSAccessKeyId", data.getString("oss_access_key_id"));
            builder.addTextBody("Signature", data.getString("signature"));
            builder.addTextBody("policy", data.getString("policy"));
            builder.addTextBody("x-oss-object-acl", data.getString("x_oss_object_acl"));
            builder.addTextBody("x-oss-forbid-overwrite", data.getString("x_oss_forbid_overwrite"));
            builder.addTextBody("key", key);
            builder.addTextBody("success_action_status", "200");
            builder.addBinaryBody("file", fileBytes, ContentType.DEFAULT_BINARY, filename);

            post.setEntity(builder.build());

            try (CloseableHttpResponse resp = http.execute(post)) {
                if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    String body = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
                    throw new RuntimeException("OSS upload failed: " + body);
                }
            }

            /* ---------- 3) Return oss:// ---------- */
            return "oss://" + key;

        } catch (Exception e) {
            throw new RuntimeException("DashScope upload failed", e);
        }
    }


    public DetectResponseDTO detect(String ossUrl) {
        DetectResponseDTO dto = new DetectResponseDTO();
        dto.setCheckPass(true);
        dto.setReason(null);
        dto.setRequestId("mock-request-id");
        return dto;
    }

    public SubmitTaskResponseDTO submitVideoSynthesis(String ossUrl, String templateId) throws java.io.IOException {
        JSONObject body = new JSONObject();
        body.put("model", "animate-anyone-gen2");

        JSONObject input = new JSONObject();
        input.put("image_url", ossUrl);
        input.put("template_id", templateId);
        body.put("input", input);

        try (CloseableHttpClient http = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(VIDEO_SYNTHESIS_URL);

            post.addHeader("Authorization", "Bearer " + apiKey);
            post.addHeader("Content-Type", "application/json");
            post.addHeader("X-DashScope-Async", "enable");
            post.addHeader("X-DashScope-OssResourceResolve", "enable");

            post.setEntity(new StringEntity(body.toString(), ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse resp = http.execute(post)) {
                String respText = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
                if (resp.getStatusLine().getStatusCode() >= 300) {
                    throw new RuntimeException(respText);
                }

                JSONObject json = new JSONObject(respText);
                JSONObject output = json.getJSONObject("output");

                SubmitTaskResponseDTO dto = new SubmitTaskResponseDTO();
                dto.setTaskId(output.getString("task_id"));
                dto.setTaskStatus(output.getString("task_status"));
                dto.setRequestId(json.optString("request_id"));
                return dto;
            }
        }
    }

    public TaskStatusResponseDTO getTaskStatus(String taskId) throws java.io.IOException {
        try (CloseableHttpClient http = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(TASKS_URL + taskId);
            get.addHeader("Authorization", "Bearer " + apiKey);

            try (CloseableHttpResponse resp = http.execute(get)) {
                String respText = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
                if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new RuntimeException(respText);
                }

                JSONObject json = new JSONObject(respText);
                JSONObject output = json.optJSONObject("output");

                TaskStatusResponseDTO dto = new TaskStatusResponseDTO();
                dto.setTaskId(taskId);
                dto.setRequestId(json.optString("request_id"));

                if (output != null) {
                    dto.setTaskStatus(output.optString("task_status"));
                    dto.setVideoUrl(output.optString("video_url"));
                }
                return dto;
            }
        }
    }
}
