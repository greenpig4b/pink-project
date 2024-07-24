package com.pinkproject.openAi;

import lombok.Data;

public class OpenAIResponse {
    @Data
    public static class AiMessageDTO {
        private String content;

        public AiMessageDTO(String aiResponse) {
            this.content = aiResponse;
        }
    }

}
