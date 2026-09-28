package net.anotheria.anosite.transfer;

/**
 * How much of the link graph a transfer covers.
 */
public enum TransferMode {

    /**
     * Only the selected document. Links are sent as they are, so a link pointing at a document the target
     * doesn't have stays dangling until that document is transferred too.
     */
    SINGLE("single", "This document only"),

    /**
     * The selected document and, recursively, everything it links to. Documents that already exist on the
     * target are overwritten, so after a deep transfer the target mirrors the source for the whole reachable
     * subgraph.
     */
    DEEP("deep", "This document and everything it links to");

    /**
     * Value the cms ui submits.
     */
    private final String parameterValue;

    /**
     * Label shown in the transfer dialog.
     */
    private final String label;

    TransferMode(final String aParameterValue, final String aLabel) {
        this.parameterValue = aParameterValue;
        this.label = aLabel;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Resolves the mode a request asks for. Anything unknown, including null, resolves to {@link #SINGLE} —
     * the transfer that changes the least.
     *
     * @param value value of the mode request parameter
     * @return the matching mode, never null
     */
    public static TransferMode fromParameter(final String value) {
        if (value != null)
            for (TransferMode mode : values())
                if (mode.parameterValue.equalsIgnoreCase(value))
                    return mode;

        return SINGLE;
    }
}
