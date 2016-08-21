/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wrmsr.bluehot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class BlueColdResource
{
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public BlueColdResource(ObjectMapper objectMapper, HttpClient httpClient)
    {
        this.objectMapper = requireNonNull(objectMapper);
        this.httpClient = requireNonNull(httpClient);
    }

    @GET
    public Response get()
    {
        try {
            HttpResponse githubResponse = httpClient.execute(new HttpGet("https://api.github.com/search/repositories?q=language:java"));
            byte[] bytes = ByteStreams.toByteArray(githubResponse.getEntity().getContent());
            Map<String, Object> map = objectMapper.readValue(bytes, new TypeReference<Map<String, Object>>() {});
            return Response.ok(objectMapper.writeValueAsBytes(map)).build();
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
