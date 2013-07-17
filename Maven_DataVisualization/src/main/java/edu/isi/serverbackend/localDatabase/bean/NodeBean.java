package edu.isi.serverbackend.localDatabase.bean;

public class NodeBean {
	private int nodeId;
	private String nodeName;
	private String uri;
	private boolean explored;
	
	public NodeBean(){
		explored = false;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public boolean isExplored() {
		return explored;
	}
	public void setExplored(boolean explored) {
		this.explored = explored;
	}
	
	
}
