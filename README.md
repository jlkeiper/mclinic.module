mclinic.module
==============

Module to allow mClinic client to query OpenMRS database and display them in the Android based devices.
A combination of #xformshelper and #odkconnector modules

The setup:
1. Central Server
- Powerful machine
- Have all obs, encounter and patient

2. Distributed Android Based Devices
- CHW in the field have mClinic to view patients information
- Different CHW have the needs for different subset of observations data
- CHW also have the needs to enter simple form
- Some forms are preferred for certain patients only

The needs:
- Sending patient, obs and preferred forms information into the phone
- Configuring to make sure different CHW gets different type of information
- Minimize the data traffic because it will be deployed in remote sites with limited connectivity

The proposed solutions:
- Configuring concepts
  - Different CHW types will be able to configure different concepts set
  - Different CHW types will be able receive obs based on the concepts set

- Configuring cohorts
  - Different CHW types will have different cohort definition.
  - For now we will use the reporting compatibility, but in the future, we need to use reporting

- Preferred forms information
  - CHW will be able to define cohort definition to decide certain cohort will have certain form marked as preferred
  - In a way this is similar to the care suggestion, clinical reminder or patient flags
