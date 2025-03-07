package org.c7exp;

import camundajar.impl.scala.App;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.community.rest.client.api.DeploymentApi;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.dto.StartProcessInstanceDto;
import org.camunda.community.rest.client.invoker.ApiClient;
import org.camunda.community.rest.client.invoker.ApiException;

import java.io.File;

public class runC7experiment {
    public static void main(String[] args) throws ApiException {

        ApiClient client = new ApiClient();
        client.setBasePath("http://localhost:8080/engine-rest");

        new DeploymentApi(client).createDeployment(
                null,
                null,
                true,
                true,
                "benchmark",
                null,
                new File(App.class.getClassLoader().getResource("src/main/resources/c7_typical-process.bpmn").getFile())
        );
        System.out.println("DEPLOYED");

        ExternalTaskClient externalTaskClient = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .maxTasks(1)
                .build();

        subscribe(externalTaskClient, "my-task-1");
        subscribe(externalTaskClient, "my-task-2");
        subscribe(externalTaskClient, "task3");
        subscribe(externalTaskClient, "my-task-4");
        subscribe(externalTaskClient, "my-task-5");
        subscribe(externalTaskClient, "my-task-6");
        subscribe(externalTaskClient, "my-task-7");
        subscribe(externalTaskClient, "my-task-8");
        subscribe(externalTaskClient, "my-task-9");
        subscribe(externalTaskClient, "my-task-10");

        ProcessDefinitionApi api = new ProcessDefinitionApi(client);

        for (int i = 1; i <= 100; i++) {

            api.startProcessInstanceByKey("typicalC7process", new StartProcessInstanceDto());

            System.out.println("STARTED instance " + i);

            System.out.println("Instance " + i + "done");

        }
    }

    private static void subscribe(ExternalTaskClient client, String topicName){
        client.subscribe(topicName)
                .lockDuration(1000)
                .handler((externalTask, externalTaskService) -> {
                    System.out.println("External Task " + topicName + " wird verarbeitet...");
                    externalTaskService.complete(externalTask);
                })
                .open();
    }
}