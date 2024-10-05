package ht.eyfout.junit.jupiter.gherkin.http;

public class GherkinHttpAPIGenerationException extends RuntimeException{
    public GherkinHttpAPIGenerationException(String msg, Throwable t){
        super(msg, t);
    }
}
