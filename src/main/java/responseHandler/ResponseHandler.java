package responseHandler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {

    public static Map<String, Object> success(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("statusCode", HttpStatus.OK.value());
        response.put("message", message);
        response.put("data", null);
        return response;
    }

    public static <T> Map<String, Object> success(String message, T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("statusCode", HttpStatus.OK.value());
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    public static <T> Map<String, Object> success(T data) {
        return success("Success", data);
    }

    public static Map<String, Object> error(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }

    public static Map<String, Object> error(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", false);
        response.put("statusCode", status.value());
        response.put("message", message);
        response.put("data", null);
        return response;
    }

    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status.is2xxSuccessful());
        map.put("statusCode", status.value());
        map.put("message", message);
        map.put("data", responseObj);
        return new ResponseEntity<>(map, status);
    }
}
