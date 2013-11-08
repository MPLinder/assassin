from django import forms
from .models import Attempt


class POCForm(forms.ModelForm):
    class Meta:
        model = Attempt
        fields = ['image']


class AttemptForm(forms.ModelForm):
    class Meta:
        model = Attempt
        fields = ['from_user', 'to_user', 'image', 'comment']