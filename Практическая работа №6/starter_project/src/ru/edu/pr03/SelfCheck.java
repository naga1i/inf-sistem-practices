package ru.edu.pr03;

public class SelfCheck {
    public static void main(String[] args) {
        boolean ok = false;
        try {
            ApprovalRouter router = new ApprovalRouter();
            boolean baseChecks = router.route(10_000, true, RiskLevel.LOW, true) == ApprovalRoute.SECURITY_REVIEW
                    && router.route(500_000, false, RiskLevel.LOW, false) == ApprovalRoute.FINANCE_REVIEW;
            boolean priorityChecks = router.route(800_000, true, RiskLevel.HIGH, true) == ApprovalRoute.SECURITY_REVIEW
                    && router.route(800_000, true, RiskLevel.HIGH, false) == ApprovalRoute.DIRECTOR_REVIEW;
            boolean boundaryChecks = router.route(499_999, false, RiskLevel.LOW, false) == ApprovalRoute.STANDARD
                    && router.route(500_000, false, RiskLevel.LOW, false) == ApprovalRoute.FINANCE_REVIEW
                    && router.route(500_001, false, RiskLevel.LOW, false) == ApprovalRoute.FINANCE_REVIEW;
            boolean variant6UrgentChecks = router.route(350_000, true, RiskLevel.MEDIUM, false) == ApprovalRoute.FAST_TRACK
                    && router.route(350_001, true, RiskLevel.MEDIUM, false) == ApprovalRoute.STANDARD
                    && router.route(499_999, true, RiskLevel.LOW, false) == ApprovalRoute.STANDARD;
            boolean negativeAmountError = false;
            try {
                router.route(0, false, RiskLevel.LOW, false);
            } catch (IllegalArgumentException e) {
                negativeAmountError = true;
            }
            boolean nullRiskError = false;
            try {
                router.route(10_000, false, null, false);
            } catch (IllegalArgumentException e) {
                nullRiskError = true;
            }
            ok = baseChecks && priorityChecks && boundaryChecks && variant6UrgentChecks && negativeAmountError && nullRiskError;
        } catch (Exception e) {
            System.out.println("FAIL exception " + e.getMessage());
        }
        System.out.println(ok ? "PASS" : "FAIL  complete TODO methods");
    }
}
