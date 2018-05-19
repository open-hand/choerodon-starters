/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.cloud.bus.event;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Stefan Pfeiffer
 */
public class UnknownRemoteApplicationEvent extends RemoteApplicationEvent {

	protected String typeInfo;
	protected byte[] payload;

	@SuppressWarnings("unused")
	private UnknownRemoteApplicationEvent() {
		super();
		this.typeInfo = null;
		this.payload = null;
	}

	public UnknownRemoteApplicationEvent(Object source, String typeInfo, byte[] payload) {
		// Initialize originService with an empty String, to avoid NullPointer in AntPathMatcher.
		super(source, "");
		this.typeInfo = typeInfo;
		this.payload = payload;
	}

	public String getTypeInfo() {
		return this.typeInfo;
	}

	public byte[] getPayload() {
		return this.payload;
	}

	public String getPayloadAsString() {
		return new String(this.payload);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		UnknownRemoteApplicationEvent that = (UnknownRemoteApplicationEvent) o;
		return Objects.equals(typeInfo, that.typeInfo) &&
				Arrays.equals(payload, that.payload);
	}

	@Override
	public int hashCode() {

		int result = Objects.hash(super.hashCode(), typeInfo);
		result = 31 * result + Arrays.hashCode(payload);
		return result;
	}
}
