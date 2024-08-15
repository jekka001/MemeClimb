package ua.corporation.memeclimb.lang;

public enum ButtonText {
    REVEAL_POOLS("revealPools"),
    TOP_UP_BALANCE("topUpBalance"),
    CHECK_BALANCE("checkBalance"),
    HOW_IT_WORKS("howItWorks"),
    WITHDRAW("withdrawButton"),
    SUPPORT("support"),
    REVEAL_POOL("revealPool"),
    MORE_POOLS("morePools"),
    BACK("back"),
    FIRST_SPIN("firstSpin"),
    SPIN("spin"),
    SPIN_NEXT("spinNext"),
    CHOSE_NEXT_POOL("chooseNextPool"),
    EARN_MORE("earnMore");

    private final String key;

    ButtonText(String key) {
        this.key = key;
    }

    public String getKey(Internationalization internationalization) {
        return internationalization.getLocalizationMessage(key);
    }
}
