package com.we.modbus.model;

public enum ErrorCode {
	/**
	 * (0x01) The function code received in the query is not an allowable action for the server.
	 */
	ILLEGAL_FUNCTION(1, "The function code received in the query is not an allowable action for the server."),
	/**
	 * (0x02) The data address received in the query is not an allowable address for the server.
	 */
	ILLEGAL_DATA_ADDRESS(2, "The data address received in the query is not an allowable address for the server."),
	/**
	 * (0x03) A value contained in the query data field is not an allowable value for server.
	 */
	ILLEGAL_DATA_VALUE(3, "A value contained in the query data field is not an allowable value for server."),
	/**
	 * (0x04) An unrecoverable error occurred while the server was attempting to perform the requested action.
	 */
	SERVER_DEVICE_FAILURE(4, "An unrecoverable error occurred while the server was attempting to perform the requested action."),
	/**
	 * (0x05) Specialized use in conjunction with programming commands.
	 * The server has accepted the request and is processing it,
	 * but a long duration of time will be required to do so.
	 */
	ACKNOWLEDGE(5, "The server has accepted the request and is processing it."),
	/**
	 * (0x06) Specialized use in conjunction with programming commands.
	 * The server is engaged in processing a long–duration program command.
	 */
	SERVER_DEVICE_BUSY(6, "The server is engaged in processing a long–duration program command."),
	/**
	 * (0x08) Specialized use in conjunction with function codes 20 and 21 and reference type 6,
	 * to indicate that the extended file area failed to pass a consistency check.
	 */
	MEMORY_PARITY_ERROR(8, "The server attempted to read record file, but detected a parity error in the memory."),
	/**
	 * Unknown error
	 */
	UKNOWN_ERROR(9, "Unknown error."),
	/**
	 * (0x0A) Specialized use in conjunction with gateways, indicates that the gateway was unable
	 * to allocate an internal communication path from the input port to the output port for processing the request.
	 */
	GATEWAY_PATH_UNAVAILABLE(10, "The gateway is misconfigured or overloaded."),
	/**
	 * (0x0B) Specialized use in conjunction with gateways,
	 * indicates that no response was obtained from the target device.
	 */
	GATEWAY_TARGET_DEVICE_FAILED_TO_RESPOND(11, "The device is not present on the network.");

	private final int code;
	private final String text;

	private ErrorCode(int code, String text) {
		this.code = code;
		this.text = text;
	}

	public static ErrorCode get(int code) {
		for (ErrorCode errorCode : ErrorCode.values()) {
			if (errorCode.getCode() == code) {
				return errorCode;
			}
		}
		return UKNOWN_ERROR;
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return text;
	}

}
