package biodiv.scheduler;

public enum SchedulerStatus {
	Scheduled("Scheduled"), Running("Running"), Aborted("Aborted"), Failed("Failed"), Success("Success");

	private String value;

	SchedulerStatus(String value) {
		this.value = value;
	}

	String value() {
		return this.value;
	}
}
