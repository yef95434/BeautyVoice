import com.google.gson.annotations.SerializedName;

public class OpenAIRequest {
    @SerializedName("prompt")
    public String prompt;

    @SerializedName("temperature")
    public double temperature;

    @SerializedName("max_tokens")
    public int maxTokens;

    public OpenAIRequest(String prompt, double temperature, int maxTokens) {
        this.prompt = prompt;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
    }
}