package com.spamdetector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spamdetector.domain.TestFile;
import com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import jakarta.ws.rs.core.Response;

@Path("/spam")
public class SpamResource {

//    your SpamDetector Class responsible for all the SpamDetecting logic
    SpamDetector detector = new SpamDetector();
    ObjectMapper jsonmapper = new ObjectMapper();


    SpamResource() throws IOException, URISyntaxException {
//        TODO: load resources, train and test to improve performance on the endpoint calls
        System.out.print("Training and testing the model, please wait");

//      TODO: call  this.trainAndTest();
        this.trainAndTest();

    }
    @GET
    @Path("/json")
    @Produces("application/json")
    public Response getSpamResults() throws IOException, URISyntaxException {
//       TODO: return the test results list of TestFile, return in a Response object
        List<TestFile> testResults = trainAndTest();

        String json = null;

        try {
            json = jsonmapper.writeValueAsString(testResults);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        Response jsonResp = Response.status(200).header("Access-Control-Allow-Origin", "http://localhost:63342")
                .header("Content-Type","application/json")
                .entity(json)
                .build();

        return jsonResp;
    }

    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() throws URISyntaxException, IOException {
//      TODO: return the accuracy of the detector, return in a Response object
        double accuracy = this.detector.getAccuracy();

        String json = null;

        try {
            json = jsonmapper.writeValueAsString(accuracy);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        Response jsonResp = Response.status(200).header("Access-Control-Allow-Origin", "http://localhost:63342")
                .header("Content-Type","application/json")
                .entity(json)
                .build();

        return jsonResp;
    }

    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() throws URISyntaxException, IOException {
       //      TODO: return the precision of the detector, return in a Response object
        double precision = this.detector.getPrecision();

        String json = null;

        try {
            json = jsonmapper.writeValueAsString(precision);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        Response jsonResp = Response.status(200).header("Access-Control-Allow-Origin", "http://localhost:63342")
                .header("Content-Type","application/json")
                .entity(json)
                .build();

        return jsonResp;
    }

    private List<TestFile> trainAndTest() throws IOException, URISyntaxException {
        if (this.detector==null){
            this.detector = new SpamDetector();
        }

//        TODO: load the main directory "data" here from the Resources folder
        URL url = this.getClass().getClassLoader().getResource("/data");
        File mainDirectory = new File(url.toURI());
        return this.detector.trainAndTest(mainDirectory);
    }
}