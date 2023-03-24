import retrofit2.Call;
import retrofit2.http.*;

public interface OpenAIChatGPTAPI {
    @POST("completions")
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer Please change to your openAIKey"
    })
    Call generateText(@Body OpenAIRequest request);
}