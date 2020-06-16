package org.vaadin.example;

import com.google.gson.Gson;
import com.googlecode.gentyref.TypeToken;
import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;



public class TaskService {

	private static TaskService instance;
	private static final Logger LOGGER = Logger.getLogger(TaskService.class.getName());

	private final HashMap<Long, Task> tasks = new HashMap<>();
	private long nextId = 0;

	public static List<Task> taskArray = new ArrayList<>();

	private TaskService() {
	}

	/**
	 * @return a reference to an example facade for Task objects.
	 **/
	public static TaskService getInstance() {
		if (instance == null) {
			instance = new TaskService();
			instance.ensureTestData();
		}
		return instance;
	}

	/**
	 * @return all available Task objects.
	 **/
	public synchronized List<Task> findAll() {
		return findAll(null);
	}

	/**
	 * Finds all Task's that match given filter.
	 **/
	public synchronized List<Task> findAll(String stringFilter) {
		ArrayList<Task> arrayList = new ArrayList<>();
		for (Task task : tasks.values()) {
			try {
				boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
						|| task.toString().toLowerCase().contains(stringFilter.toLowerCase());
				if (passesFilter) {
					arrayList.add(task.clone());
				}
			} catch (CloneNotSupportedException ex) {
				Logger.getLogger(TaskService.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		Collections.sort(arrayList, new Comparator<Task>() {

			@Override
			public int compare(Task o1, Task o2) {
				return (int) (o2.getId() - o1.getId());
			}
		});
		return arrayList;
	}

	/**
	 * @return the amount of all tasks in the system
	 **/
	public synchronized long count() {
		return tasks.size();
	}

	/**
	 * Deletes a task from a system
	 **/
	public synchronized void delete(Task value) throws IOException, InterruptedException {
		tasks.remove(value.getId());
		deleteTask(value);
	}

	/**
	 * Persists or updates task in the system. Also assigns an identifier
	 * for new Task instances.
	 **/
	public synchronized void save(Task entry) {
		if (entry == null) {
			LOGGER.log(Level.SEVERE,
					"Task is null.");
			return;
		}
		if (entry.getId() == null) {
			entry.setId(nextId++);
		}
		try {
			entry = (Task) entry.clone();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		tasks.put(entry.getId(), entry);
	}

	/**
	 * GET and data processing
	 **/
	public void ensureTestData() {
		if (findAll().isEmpty()) {

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8001/v1/task/")).build();
			client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
					.thenApply(HttpResponse::body)
					.thenApply(TaskService::parseTasks)
					.join();

			for(Task task : taskArray){
				save(task);
			}
		}
	}

	public static List<Task> parseTasks(String responseBody){
		// Actual Parse
		JSONArray tasks = new JSONArray(responseBody);

		Gson gson = new Gson();
		Type taskListType = new TypeToken<ArrayList<Task>>(){}.getType();

		taskArray = gson.fromJson(String.valueOf(tasks), taskListType);

		return taskArray;

	}

	/**
	 * POST
	 **/
	public static void postTask(Task entry) throws IOException, InterruptedException {

		String title = entry.getTaskTitle().toLowerCase();
		String description = entry.getTaskDescription().toLowerCase();
		String deadline = entry.getDeadLine().toString().toLowerCase();
		String list = entry.getList().toLowerCase();
		String priority = entry.getPriority().toString().toLowerCase();
		String status = entry.getStatus().toString().toLowerCase();
		//deleteTask(entry);

		String postEndpoint = "http://localhost:8001/v1/task/?taskTitle=" + title + "&taskDescription=" + description +
				"&deadLine=" + deadline + "&list=" + list + "&priority=" + priority + "&status=" + status;

		String inputJson = new Gson().toJson(entry);
		//System.out.println(inputJson);

		var request = HttpRequest.newBuilder()
				.uri(URI.create(postEndpoint))
				// .header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(inputJson))
				.build();

		var client = HttpClient.newHttpClient();

		client.send(request, HttpResponse.BodyHandlers.ofString());

	}

	/**
	 * DELETE
	 **/
	public static void deleteTask(Task entry) throws IOException, InterruptedException {
		String title = entry.getTaskTitle();

		String deleteEndpoint = "http://localhost:8001/v1/task/?taskTitle=" + title;
		//System.out.println(deleteEndpoint);

		var request = HttpRequest.newBuilder()
				.uri(URI.create(deleteEndpoint))
				.header("Content-Type", "application/json")
				.DELETE()
				.build();

		var client = HttpClient.newHttpClient();

		client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	/**
	 * PUT
	 **/
	public static synchronized void updateTask(Task entry) throws IOException, InterruptedException {
		String title = entry.getTaskTitle().toLowerCase();
		String description = entry.getTaskDescription().toLowerCase();
		String deadline = entry.getDeadLine().toString().toLowerCase();
		String list = entry.getList().toLowerCase();
		String priority = entry.getPriority().toString().toLowerCase();
		String status = entry.getStatus().toString().toLowerCase();

		String putEndpoint = "http://localhost:8001/v1/task/?taskTitle=" + title + "&taskDescription=" + description +
				"&deadLine=" + deadline + "&list=" + list + "&priority=" + priority + "&status=" + status;

		String inputJson = new Gson().toJson(entry);
		//System.out.println(inputJson);

		var request = HttpRequest.newBuilder()
				.uri(URI.create(putEndpoint))
				.header("Content-Type", "application/json")
				.PUT(HttpRequest.BodyPublishers.ofString(inputJson))
				.build();

		var client = HttpClient.newHttpClient();

		client.send(request, HttpResponse.BodyHandlers.ofString());
	}

}