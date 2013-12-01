# Confidence level necessary for attempt to succeed.
SUCCESS_CONFIDENCE = 40.00
# Scale from which confidence percent is mapped
SUCCESS_CONFIDENCE_SCALE = (0, SUCCESS_CONFIDENCE)

# Minimum percentage confidence assigned to successful attempts
SUCCESS_PERCENT = 80
# Scale onto which confidence level is mapped
SUCCESS_PERCENT_SCALE = (0, 100-SUCCESS_PERCENT)

# Confidence level above which confidence percent is zero
ZERO_CONFIDENCE = 100.00

# Scale from which confidence percent is mapped for failed attempts
FAIL_CONFIDENCE_SCALE = (SUCCESS_CONFIDENCE, ZERO_CONFIDENCE)
# Scale onto which confidence level is mapped for failed attempts
FAIL_PERCENT_SCALE = (0, SUCCESS_PERCENT)

TRAINING_IMAGES_REQUIRED = 3

SUCCESS_POINTS = 100

LEADERBOARD_ENTRIES = 10