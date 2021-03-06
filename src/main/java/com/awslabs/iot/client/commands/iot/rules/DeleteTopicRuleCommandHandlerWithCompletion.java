package com.awslabs.iot.client.commands.iot.rules;

import com.awslabs.general.helpers.interfaces.IoHelper;
import com.awslabs.iot.client.commands.iot.RuleCommandHandlerWithCompletion;
import com.awslabs.iot.client.commands.iot.completers.RuleCompleter;
import com.awslabs.iot.client.parameters.interfaces.ParameterExtractor;
import com.awslabs.iot.data.ImmutableRuleName;
import com.awslabs.iot.data.RuleName;
import com.awslabs.iot.helpers.interfaces.V2IotHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

public class DeleteTopicRuleCommandHandlerWithCompletion implements RuleCommandHandlerWithCompletion {
    private static final String DELETETOPICRULE = "delete-topic-rule";
    private static final int TOPIC_RULE_NAME_POSITION = 0;
    private static final Logger log = LoggerFactory.getLogger(DeleteTopicRuleCommandHandlerWithCompletion.class);
    @Inject
    ParameterExtractor parameterExtractor;
    @Inject
    IoHelper ioHelper;
    @Inject
    RuleCompleter ruleCompleter;
    @Inject
    V2IotHelper v2IotHelper;

    @Inject
    public DeleteTopicRuleCommandHandlerWithCompletion() {
    }

    @Override
    public void innerHandle(String input) {
        List<String> parameters = parameterExtractor.getParameters(input);

        RuleName topicRuleName = ImmutableRuleName.builder().name(parameters.get(TOPIC_RULE_NAME_POSITION)).build();

        v2IotHelper.deleteTopicRule(topicRuleName);
    }

    @Override
    public void showUsage(Logger logger) {
        log.info("You must specify the name of the topic rule you want to delete.");
    }

    @Override
    public String getCommandString() {
        return DELETETOPICRULE;
    }

    @Override
    public String getHelp() {
        return "Deletes a rule by name.";
    }

    @Override
    public int requiredParameters() {
        return 1;
    }

    public ParameterExtractor getParameterExtractor() {
        return this.parameterExtractor;
    }

    public IoHelper getIoHelper() {
        return this.ioHelper;
    }

    public RuleCompleter getRuleCompleter() {
        return this.ruleCompleter;
    }
}
