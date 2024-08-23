package net.anotheria.anosite.cms.translation;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import net.anotheria.anosite.config.LocalizationAutoTranslationTokenConfig;
import net.anotheria.anosite.gen.shared.service.BasicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class IASGTranslationTranslationServiceImpl extends BasicService implements IASGTranslationService {

    private static IASGTranslationTranslationServiceImpl instance;

    private static final Logger log = LoggerFactory.getLogger(IASGTranslationTranslationServiceImpl.class);
    private static final String PROMPT = "You will get a localizations in format 'key'='translation'. Translate it from %s to %s without any explanations.\n" +
            "Localizations:\n \"\"\"\n%s\n\"\"\"";

    private LocalizationAutoTranslationTokenConfig config;
    private OpenAiService openAiService;

    public IASGTranslationTranslationServiceImpl() {
        this.config = LocalizationAutoTranslationTokenConfig.getInstance();
        this.openAiService = new OpenAiService(config.getToken(), Duration.ofSeconds(240));
    }

    @Override
    public String translate(String originLanguage, String targetLanguage, String bundleContent) {
        String result = "";
        String formattedPrompt = String.format(PROMPT, originLanguage, targetLanguage, bundleContent);

        List<ChatMessage> chatMessageList = new ArrayList<>();
        chatMessageList.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), formattedPrompt));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(chatMessageList)
                .user("testing")
                .temperature(0.0)
                .build();

        int maxAttempts = 15;
        int currentAttempt = 1;
        while (currentAttempt <= maxAttempts) {
            try {
                List<ChatCompletionChoice> choices = openAiService.createChatCompletion(chatCompletionRequest).getChoices();
                if (!choices.isEmpty()) {
                    for (ChatCompletionChoice choice : choices) {
                        return choice.getMessage().getContent();
                    }
                }
            } catch (RuntimeException ex) {
                if (ex.getMessage().toLowerCase().contains("http") || ex.getMessage().toLowerCase().contains("timeout") || ex.getMessage().toLowerCase().contains("that model is currently overloaded with other requests")) {
                    log.error("Cannot translate localizations. Exception: {}.\n Attempt #{}", ex.getMessage(), currentAttempt);
                    currentAttempt++; // request to openAI can often fail. So will try one more time. There are only 15 attempts.
                } else {
                    log.error("Cannot translate localizations. Exception: {}.", ex.getMessage());
                    break;
                }
            }
        }

        return result;
    }

    static IASGTranslationTranslationServiceImpl getInstance() {
        if (instance == null) {
            instance = new IASGTranslationTranslationServiceImpl();
        }
        return instance;
    }
}
