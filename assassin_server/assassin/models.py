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
