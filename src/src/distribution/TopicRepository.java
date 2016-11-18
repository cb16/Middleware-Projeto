package distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TopicRepository {
	private ArrayList<String> topics;
	private Map<String, ArrayList<Message>> topicPublicationsRepo;
	private Map<String, ArrayList<SubscribeUser>> topicSubscribersRepo;
	
	public TopicRepository() {
		this.topics = new ArrayList<String>();
		this.topicPublicationsRepo = new HashMap<String, ArrayList<Message>>();
		this.topicSubscribersRepo = new HashMap<String, ArrayList<SubscribeUser>>();
	}

	public ArrayList<String> getTopics() {
		return topics;
	}

	public Map<String, ArrayList<Message>> getTopicPublicationsRepo() {
		return topicPublicationsRepo;
	}

	public Map<String, ArrayList<SubscribeUser>> getTopicSubscribersRepo() {
		return topicSubscribersRepo;
	}

	public void addTopic(String topic) {
		this.topics.add(topic);
		
	}

	public void addTopicSubscribe(String topic) {
		ArrayList<SubscribeUser> arrayList = new ArrayList<SubscribeUser>();
		this.topicSubscribersRepo.put(topic, arrayList);
	}

	public void addTopicPublish(String topic) {
		ArrayList<Message> messages = new ArrayList<Message>();
		this.topicPublicationsRepo.put(topic, messages);
	}

	public void addSubscriber(String topic, SubscribeUser user) {
		ArrayList<SubscribeUser> subscribers = topicSubscribersRepo.get(topic);
		subscribers.add(user);
		topicSubscribersRepo.put(topic, subscribers);
	}

	public void addPublication(String topic, Message message) {
		ArrayList<Message> publications = topicPublicationsRepo.get(topic);
		publications.add(message);
		topicPublicationsRepo.put(topic, publications);
	}

}
