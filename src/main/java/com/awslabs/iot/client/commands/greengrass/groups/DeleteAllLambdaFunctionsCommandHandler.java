package com.awslabs.iot.client.commands.greengrass.groups;

import com.amazonaws.services.greengrass.model.GroupInformation;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.DeleteFunctionRequest;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.lambda.model.ListFunctionsRequest;
import com.awslabs.aws.iot.resultsiterator.helpers.interfaces.IoHelper;
import com.awslabs.aws.iot.resultsiterator.helpers.v1.V1ResultsIterator;
import com.awslabs.aws.iot.resultsiterator.helpers.v1.interfaces.V1GreengrassHelper;
import com.awslabs.iot.client.commands.greengrass.GreengrassCommandHandler;
import com.awslabs.iot.client.parameters.interfaces.ParameterExtractor;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteAllLambdaFunctionsCommandHandler implements GreengrassCommandHandler {
    private static final String DELETE_ALL_LAMBDA_FUNCTIONS = "delete-all-lambda-functions";
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(DeleteAllLambdaFunctionsCommandHandler.class);
    @Inject
    V1GreengrassHelper greengrassHelper;
    @Inject
    ParameterExtractor parameterExtractor;
    @Inject
    IoHelper ioHelper;
    @Inject
    AWSLambdaClient awsLambdaClient;

    @Inject
    public DeleteAllLambdaFunctionsCommandHandler() {
    }

    @Override
    public void innerHandle(String input) {
        ListFunctionsRequest listFunctionsRequest = new ListFunctionsRequest();

        List<FunctionConfiguration> functionConfigurations = new V1ResultsIterator<FunctionConfiguration>(awsLambdaClient, listFunctionsRequest)
                .stream().collect(Collectors.toList());

        List<String> groupNames = greengrassHelper.listGroups()
        .map(GroupInformation::getName)
                .collect(Collectors.toList());

        List<String> immutableGroupList = groupNames.stream()
                .filter(groupName -> greengrassHelper.isGroupImmutable(groupName))
                .collect(Collectors.toList());

        immutableGroupList.forEach(groupName -> log.info("Skipping group [" + groupName + "] because it is an immutable group"));

        groupNames.stream()
                // Remove the immutable groups
                .filter(groupName -> !immutableGroupList.contains(groupName))
                // Delete each group's Lambda functions
                .forEach(groupName -> deleteGroupLambdas(functionConfigurations, groupName));
    }

    private void deleteGroupLambdas(List<FunctionConfiguration> functionConfigurations, String groupName) {
        String pattern = String.join(" ", "_", groupName + ".*");

        functionConfigurations.stream()
                .filter(functionConfiguration -> functionConfiguration.getFunctionName().matches(pattern))
                .forEach(this::deleteFunction);

        log.info("Deleted functions for group [" + groupName + "]");
    }

    private void deleteFunction(FunctionConfiguration functionConfiguration) {
        String name = functionConfiguration.getFunctionName();

        log.info("Deleting function: " + name);

        DeleteFunctionRequest deleteFunctionRequest = new DeleteFunctionRequest()
                .withFunctionName(name);

        awsLambdaClient.deleteFunction(deleteFunctionRequest);
    }

    @Override
    public String getCommandString() {
        return DELETE_ALL_LAMBDA_FUNCTIONS;
    }

    @Override
    public String getHelp() {
        return "Deletes all Lambda functions associated with a Greengrass group.";
    }

    @Override
    public int requiredParameters() {
        return 0;
    }

    public ParameterExtractor getParameterExtractor() {
        return this.parameterExtractor;
    }

    public IoHelper getIoHelper() {
        return this.ioHelper;
    }
}
