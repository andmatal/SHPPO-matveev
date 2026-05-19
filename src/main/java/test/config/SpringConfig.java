package test.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import test.factory.QuestionFactory;
import test.factory.QuestionFactoryImpl;
import test.observer.ResultLogger;
import test.observer.TestObserver;
import test.strategy.PartialScoringStrategy;
import test.strategy.ScoringStrategy;
import test.strategy.StrictScoringStrategy;
@Configuration
@EnableAspectJAutoProxy
public class SpringConfig {
    @Bean
    public QuestionFactory questionFactory() {
        return new QuestionFactoryImpl();
    }
    @Bean("strictStrategy")
    public ScoringStrategy strictScoringStrategy() {
        return new StrictScoringStrategy();
    }
    @Bean("partialStrategy")
    public ScoringStrategy partialScoringStrategy() {
        return new PartialScoringStrategy();
    }
    @Bean
    public TestObserver resultLogger() {
        return new ResultLogger();
    }
}

