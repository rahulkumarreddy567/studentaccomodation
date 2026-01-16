package com.pandalodge.model;

public class FAQ {
    private int id;
    private String question;
    private String answer;
    private String category;
    private int orderIndex;

    public FAQ(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.category = "GENERAL";
    }

    public FAQ(int id, String question, String answer, String category) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.category = category;
    }

    public FAQ(int id, String question, String answer, String category, int orderIndex) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.orderIndex = orderIndex;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getCategory() {
        return category;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getFormattedCategory() {
        switch (category) {
            case "BOOKING":
                return "📅 Booking";
            case "SECURITY":
                return "🔒 Security";
            case "ACCOMMODATION":
                return "🏠 Accommodation";
            default:
                return "ℹ️ General";
        }
    }

    @Override
    public String toString() {
        return "Q: " + question;
    }
}
