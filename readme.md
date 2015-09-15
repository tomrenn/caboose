caboose
---------

**Build a Google compute instance template with api-container**

```
gcloud compute instance-templates create api-template \
--image container-vm \
--metadata-from-file google-container-manifest=api-container.yml \
--machine-type f1-micro \
--tags http-server
```

