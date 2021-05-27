public class RandomNumberSaver {

    private int randomNumber;
    private int ttl;

    public RandomNumberSaver(int randomNumber, int ttl) {
        this.randomNumber = randomNumber;
        this.ttl = ttl;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
