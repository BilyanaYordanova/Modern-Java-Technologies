package bg.sofia.uni.fmi.mjt.data;

public class AudioFormatDataHolder {

	private int channels;
	private String encoding;
	private float frameRate;
	private int frameSize;
	private float sampleRate;
	private int sampleSizeInBits;
	private boolean isBigEndian;

	public AudioFormatDataHolder(int channels, String encoding, float frameRate, int frameSize, float sampleRate,
			int sampleSizeInBits, boolean isBigEndian) {
		super();
		this.channels = channels;
		this.encoding = encoding;
		this.frameRate = frameRate;
		this.frameSize = frameSize;
		this.sampleRate = sampleRate;
		this.sampleSizeInBits = sampleSizeInBits;
		this.isBigEndian = isBigEndian;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public float getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(float frameRate) {
		this.frameRate = frameRate;
	}

	public int getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(int frameSize) {
		this.frameSize = frameSize;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}

	public int getSampleSizeInBits() {
		return sampleSizeInBits;
	}

	public void setSampleSizeInBits(int sampleSizeInBits) {
		this.sampleSizeInBits = sampleSizeInBits;
	}

	public boolean isBigEndian() {
		return isBigEndian;
	}

	public void setBigEndian(boolean isBigEndian) {
		this.isBigEndian = isBigEndian;
	}
}
