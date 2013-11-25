import models
from django import forms


class POCForm(forms.ModelForm):
    class Meta:
        model = models.Attempt
        fields = ['image']


class AttemptForm(forms.ModelForm):
    class Meta:
        model = models.Attempt
        fields = ['to_user', 'image', 'comment']


class TrainingImageForm(forms.ModelForm):
    class Meta:
        model = models.TrainingImage
        fields = ['image']