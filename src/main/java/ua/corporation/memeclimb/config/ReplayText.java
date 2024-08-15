package ua.corporation.memeclimb.config;

public enum ReplayText {
    CLIENT_PUBLIC_KEY("<Your Wallet Address>"),
    BALANCE("<balance>"),
    POOL("<pool>"),
    STEP("<step>"),
    COUNT_OF_STEP("<countOfStep>"),
    INITIAL_FEE("<initialFee>"),
    TOP_REWARD("<topReward>"),
    NUMBER_STEPS("<numberSteps>"),
    POOL_NAME("<poolName>"),
    POOL_REWARD("<poolReward>"),
    TIME("<time>"),
    SPIN_FEE("<spinFee>");

    private final String key;

    ReplayText(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
