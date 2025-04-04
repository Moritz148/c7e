package org.c7exp;

import camundajar.impl.scala.App;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.community.rest.client.api.DeploymentApi;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.api.ProcessInstanceApi;
import org.camunda.community.rest.client.dto.*;
import org.camunda.community.rest.client.invoker.ApiClient;
import org.camunda.community.rest.client.invoker.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.CountDownLatch;

public class runC7experiment {
    static {
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss.SSS");
    }
    static CountDownLatch latch = new CountDownLatch(1); //100*10 Instanzen
    static ApiClient apiClient = new ApiClient();
    static int instancesCounter = 0;
    private static final Logger logger = LoggerFactory.getLogger(runC7experiment.class);


    public static void main(String[] args) throws ApiException, IOException, InterruptedException {


        apiClient.setBasePath("http://camunda7:8080/engine-rest");
//        InputStream bpmnStream = App.class.getClassLoader().getResourceAsStream("c7ex.bpmn");
//        File bpmnfile = new File((App.class.getClassLoader().getResource("c7ex.bpmn")).getFile());


        // BPMN-Datei aus dem Classpath als InputStream laden
        InputStream bpmnStream = App.class.getClassLoader().getResourceAsStream("C7-test.bpmn");

        if (bpmnStream == null) {
            throw new RuntimeException("BPMN file not found: c7ex.bpmn");
        }

        // Temporäre Datei erstellen, um den InputStream zu speichern
        //File tempFile = File.createTempFile("c7ex", ".bpmn");
        File tempFile = File.createTempFile("C7-test", ".bpmn");

        tempFile.deleteOnExit(); // Datei wird beim Beenden des Programms gelöscht

        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bpmnStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }


        new DeploymentApi(apiClient).createDeployment(
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
        logger.info("Process deployed");
        //System.out.println("DEPLOYED");

        /*ExternalTaskClient externalTaskClient1 = ExternalTaskClient.create()
                .baseUrl("http://camunda7:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .maxTasks(100)
                .build();
        ExternalTaskClient externalTaskClient2 = ExternalTaskClient.create()
                .baseUrl("http://camunda7:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .maxTasks(100)
                .build();
        ExternalTaskClient externalTaskClient3 = ExternalTaskClient.create()
                .baseUrl("http://camunda7:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .maxTasks(100)
                .build();
        ExternalTaskClient externalTaskClient4 = ExternalTaskClient.create()
                .baseUrl("http://camunda7:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .maxTasks(100)
                .build();
        ExternalTaskClient externalTaskClient5 = ExternalTaskClient.create()
                .baseUrl("http://camunda7:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .maxTasks(100)
                .build();
        ExternalTaskClient externalTaskClient6 = ExternalTaskClient.create()
                .baseUrl("http://camunda7:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .maxTasks(100)
                .build();
        ExternalTaskClient externalTaskClient7 = ExternalTaskClient.create()
                .baseUrl("http://camunda7:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .maxTasks(100)
                .build();
        ExternalTaskClient externalTaskClient8 = ExternalTaskClient.create()
                .baseUrl("http://camunda7:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .maxTasks(100)
                .build();
        ExternalTaskClient externalTaskClient9 = ExternalTaskClient.create()
                .baseUrl("http://camunda7:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .maxTasks(100)
                .build();
        ExternalTaskClient externalTaskClient10 = ExternalTaskClient.create()
                .baseUrl("http://camunda7:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .maxTasks(100)
                .build();

        subscribe(externalTaskClient1, "my-task-1");
        subscribe(externalTaskClient2, "my-task-2");
        subscribe(externalTaskClient3, "task3");
        subscribe(externalTaskClient4, "my-task-4");
        subscribe(externalTaskClient5, "my-task-5");
        subscribe(externalTaskClient6, "my-task-6");
        subscribe(externalTaskClient7, "my-task-7");
        subscribe(externalTaskClient8, "my-task-8");
        subscribe(externalTaskClient9, "my-task-9");
        subscribe(externalTaskClient10, "my-task-10");*/
        ProcessInstanceApi instanceApi = new ProcessInstanceApi(apiClient);

        ProcessDefinitionApi api = new ProcessDefinitionApi(apiClient);

        //api.startProcessInstanceByKey("typicalC7process", new StartProcessInstanceDto());

        api.startProcessInstanceByKey("Process_1yjzvci", new StartProcessInstanceDto());
        instancesCounter++;
        logger.info("Instance #{} gestartet", instancesCounter);
        //System.out.println("Instance #" + instancesCounter + " gestartet");


        // CountDownLatch, um nach allen 100 Instanzen zu warten
        //final int processCount = 100;
        /*for (int i = 1; i <= 100; i++) {
            ProcessInstanceWithVariablesDto PI = api.startProcessInstanceByKey("typicalC7process", new StartProcessInstanceDto());
            //ProcessInstanceWithVariablesDto PI2 = api.startProcessInstanceByKey("typicalC7process", new StartProcessInstanceDto());
            //api.startProcessInstanceByKey("typicalC7process", new StartProcessInstanceDto());
            String processInstanceId = PI.getId();

            System.out.println("STARTED instance " + i + " ID: " + processInstanceId);
            */
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
            System.out.println(isCompleted);*//**/
        latch.countDown();
        latch.await();

        //System.out.println("Alle Prozesse und Tasks abgeschlossen, das Programm wird beendet.");
        logger.info("Alle Prozesse und Tasks abgeschlossen, das Programm wird beendet.");
        // Beende den ExternalTaskClient
        /*externalTaskClient1.stop();
        externalTaskClient2.stop();
        externalTaskClient3.stop();
        externalTaskClient4.stop();
        externalTaskClient5.stop();
        externalTaskClient6.stop();
        externalTaskClient7.stop();
        externalTaskClient8.stop();
        externalTaskClient9.stop();
        externalTaskClient10.stop();*/

        // Programm beenden
        System.exit(0);
    }

    private static void subscribe(ExternalTaskClient client, String topicName){
        if (topicName == "my-task-10") {
            client.subscribe(topicName)
                    .lockDuration(1000)
                    .handler((externalTask, externalTaskService) -> {
                        //System.out.println("External Task " + topicName + " wird verarbeitet...");
                        logger.info("External Task {} wird verarbeitet...", topicName);
                        externalTaskService.complete(externalTask);

                        latch.countDown();
                        if (instancesCounter < 10){
                        ProcessDefinitionApi api = new ProcessDefinitionApi(apiClient);
                        try {
                            api.startProcessInstanceByKey("typicalC7process", new StartProcessInstanceDto());
                            instancesCounter++;
                            logger.info("Instance #{} gestartet", instancesCounter);
                            //System.out.println("Instance #" + instancesCounter + " gestartet");
                        } catch (ApiException e) {
                            throw new RuntimeException(e);
                        }}
                    })
                    .open();
        } else {
            client.subscribe(topicName)
                    .lockDuration(1000)
                    .handler((externalTask, externalTaskService) -> {
                        //System.out.println("External Task " + topicName + " wird verarbeitet...");
                        logger.info("External Task {} wird verarbeitet...", topicName);
                        externalTaskService.complete(externalTask);
                        latch.countDown();
                    })
                    .open();

        }
    }
}