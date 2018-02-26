package biodiv.scheduler;

public enum SchedulerStatus {
	SCHEDULED("SCHEDULED"), RUNNING("RUNNING"), ABORTED("ABORTED"), FAILED("FAILED"), SUCCESS("SUCCESS");

	private String value;

	SchedulerStatus(String value) {
		this.value = value;
	}

	String value() {
		return this.value;
	}
}
