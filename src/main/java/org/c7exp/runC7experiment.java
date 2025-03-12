package org.c7exp;

import camundajar.impl.scala.App;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.community.rest.client.api.DeploymentApi;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.api.ProcessInstanceApi;
import org.camunda.community.rest.client.dto.*;
import org.camunda.community.rest.client.invoker.ApiClient;
import org.camunda.community.rest.client.invoker.ApiException;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class runC7experiment {
    static CountDownLatch latch = new CountDownLatch(1000);
    public static void main(String[] args) throws ApiException, IOException, InterruptedException {

        ApiClient client = new ApiClient();
        client.setBasePath("http://camunda7platform:8080/engine-rest");
//        InputStream bpmnStream = App.class.getClassLoader().getResourceAsStream("c7ex.bpmn");
//        File bpmnfile = new File((App.class.getClassLoader().getResource("c7ex.bpmn")).getFile());


        // BPMN-Datei aus dem Classpath als InputStream laden
        InputStream bpmnStream = App.class.getClassLoader().getResourceAsStream("c7ex.bpmn");

        if (bpmnStream == null) {
            throw new RuntimeException("BPMN file not found: c7ex.bpmn");
        }

        // Temporäre Datei erstellen, um den InputStream zu speichern
        File tempFile = File.createTempFile("c7ex", ".bpmn");
        tempFile.deleteOnExit(); // Datei wird beim Beenden des Programms gelöscht

        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bpmnStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }


        new DeploymentApi(client).createDeployment(
                null,
                null,
                true,
                true,
                "benchmark",
                null,
//               new File(App.class.getClassLoader().getResource("c7ex.bpmn").getFile()));
//              bpmnfile
                tempFile
        );
        System.out.println("DEPLOYED");

        ExternalTaskClient externalTaskClient = ExternalTaskClient.create()
                .baseUrl("http://camunda7platform:8080/engine-rest")
                .asyncResponseTimeout(10000)
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
        ProcessInstanceApi instanceApi = new ProcessInstanceApi(client);


        // CountDownLatch, um nach allen 100 Instanzen zu warten
        //final int processCount = 100;

        for (int i = 1; i <= 100; i++) {
            ProcessInstanceWithVariablesDto PI = api.startProcessInstanceByKey("typicalC7process", new StartProcessInstanceDto());
            //ProcessInstanceWithVariablesDto PI2 = api.startProcessInstanceByKey("typicalC7process", new StartProcessInstanceDto());
            //api.startProcessInstanceByKey("typicalC7process", new StartProcessInstanceDto());
            String processInstanceId = PI.getId();

            System.out.println("STARTED instance " + i + " ID: " + processInstanceId);
            /*System.out.println("STARTED instance " + "ID: " + PI2.getId());

            boolean isCompleted = false;
            System.out.println(isCompleted + "1");
            ProcessInstanceDto instance = instanceApi.getProcessInstance(processInstanceId);

            while (!isCompleted) {
                // Hole die Prozessinstanz
                System.out.println(isCompleted + "2");
                System.out.println("ended? " + instance.getEnded());
                // Wenn der Prozess beendet ist

                if (instance.ended(true))
                {
                    System.out.println(isCompleted + "3");
                    isCompleted = true;
                } else {
                    // Warten und dann erneut prüfen
                    Thread.sleep(1000); // 2 Sekunden warten
                }
            }
            System.out.println(isCompleted);*/

            System.out.println("Instance " + i + "done");

        }
        latch.await();
        System.out.println("Alle Prozesse und Tasks abgeschlossen, das Programm wird beendet.");

        // Beende den ExternalTaskClient
        externalTaskClient.stop();

        // Programm beenden
        System.exit(0);
    }

    private static void subscribe(ExternalTaskClient client, String topicName){
        client.subscribe(topicName)
                .lockDuration(1000)
                .handler((externalTask, externalTaskService) -> {
                    System.out.println("External Task " + topicName + " wird verarbeitet...");
                    externalTaskService.complete(externalTask);

                    latch.countDown();
                })
                .open();
    }
}