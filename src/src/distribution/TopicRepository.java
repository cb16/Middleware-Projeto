package distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TopicRepository {
	private ArrayList<String> topics;
	private Map<String, ArrayList<Message>> topicPublicationsRepo;
	private Map<String, ArrayList<PublisherUser>> topicPublisherRepo;
	
	public TopicRepository() {
		this.topics = new ArrayList<String>();
		this.topicPublicationsRepo = new HashMap<String, ArrayList<Message>>();
		this.topicPublisherRepo = new HashMap<String, ArrayList<PublisherUser>>();
	}

	public ArrayList<String> getTopics() {
		return topics;
	}

	public Map<String, ArrayList<Message>> getTopicPublicationsRepo() {
		return topicPublicationsRepo;
	}

	public Map<String, ArrayList<PublisherUser>> getTopicPublisherRepo() {
		return topicPublisherRepo;
	}

	public void addTopic(String topic) {
		this.topics.add(topic);
		
	}

}
