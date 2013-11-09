from utils import get_confidence_level, scale

from django.db import models
from django.contrib.auth.models import User


class TrainingImage(models.Model):
    user = models.ForeignKey(User)
    image = models.FileField(upload_to='training_images')
    created_date = models.DateTimeField(auto_now_add=True)


class Attempt(models.Model):
    from_user = models.ForeignKey(User, related_name='attempts_made')
    to_user = models.ForeignKey(User, related_name='attempts_on')
    image = models.FileField(upload_to='attempts')
    comment = models.CharField(max_length=200, blank=True, null=True)
    created_date = models.DateTimeField(auto_now_add=True)
    confidence_level = models.FloatField(blank=True, null=True)

    def get_confidence(self):
        # TODO: don't hard code this shit
        if not self.confidence_level or self.confidence_level > 100:
            return '0%', False
        elif self.confidence_level == 0:
            return '100%', False
        elif self.confidence_level < 35.00:
            scaled_val = scale(self.confidence_level, range(0, 35), range(0, 20))
            perc = 100 * (scaled_val/20)
            return '%.2f' % perc + '%', True
        else:
            scaled_val = scale(self.confidence_level, range(35, 100), range(0, 80))
            perc = 80 - scaled_val
            return '%.2f' % perc + '%', False

    def save(self):
        # Call save here so that the file has a path that cv2 can read from
        super(Attempt, self).save()
        if not self.confidence_level:
            self.confidence_level = get_confidence_level(self.to_user, self.image)
            super(Attempt, self).save()
