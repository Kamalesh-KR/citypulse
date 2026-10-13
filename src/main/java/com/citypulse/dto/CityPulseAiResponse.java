package com.citypulse.dto;

public class CityPulseAiResponse {

    private String question;
    private String answer;
    private String dataSource;
    private String responseType;

    public CityPulseAiResponse() {
    }

    public CityPulseAiResponse(
            String question,
            String answer,
            String dataSource,
            String responseType) {

        this.question = question;
        this.answer = answer;
        this.dataSource = dataSource;
        this.responseType = responseType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }
}