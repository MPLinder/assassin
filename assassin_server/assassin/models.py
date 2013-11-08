from utils import get_confidence_level

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

    def save(self):
        # Call save here so that the file has a path that cv2 can read from
        super(Attempt, self).save()
        if not self.confidence_level:
            self.confidence_level = get_confidence_level(self.to_user, self.image)
            super(Attempt, self).save()
