package io.spring.gemfire.rest.app.exception;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataError
{
	private String message;
	private String stackTrace;
	private String error;
	private String  path;
}
