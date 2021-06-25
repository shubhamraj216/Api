package com.hitit.api;

import java.net.URI;

public class Hit {
	private URI url;
	private boolean isParallel;
	private int count;
	
	public Hit(URI url, boolean isParallel, int count) {
		super();
		this.url = url;
		this.isParallel = isParallel;
		this.count = count;
	}
	
	public URI getUrl() {
		return url;
	}
	public boolean isParallel() {
		return isParallel;
	}
	public int getCount() {
		return count;
	}

	@Override
	public String toString() {
		return "Hit [url=" + url + ", isParallel=" + isParallel + ", count=" + count + "]";
	}
	
}
