# AndroidAPS

* Discuss the Dynamic ISF version at: https://discord.gg/Q7gYDh9Yq3

* Check the wiki: https://androidaps.readthedocs.io
*  Everyone who’s been looping with AndroidAPS needs to fill out the form after 3 days of looping  https://docs.google.com/forms/d/14KcMjlINPMJHVt28MDRupa4sz4DDIooI4SrW0P3HSN8/viewform?c=0&w=1

[![Support Server](https://img.shields.io/discord/629952586895851530.svg?label=Discord&logo=Discord&colorB=7289da&style=for-the-badge)](https://discord.gg/4fQUWHZ4Mw)

[![Build status](https://travis-ci.org/nightscout/AndroidAPS.svg?branch=master)](https://travis-ci.org/nightscout/AndroidAPS)
[![Crowdin](https://d322cqt584bo4o.cloudfront.net/androidaps/localized.svg)](https://translations.androidaps.org/project/androidaps)
[![Documentation Status](https://readthedocs.org/projects/androidaps/badge/?version=latest)](https://androidaps.readthedocs.io/en/latest/?badge=latest)
[![codecov](https://codecov.io/gh/MilosKozak/AndroidAPS/branch/master/graph/badge.svg)](https://codecov.io/gh/MilosKozak/AndroidAPS)
dev: [![codecov](https://codecov.io/gh/MilosKozak/AndroidAPS/branch/dev/graph/badge.svg)](https://codecov.io/gh/MilosKozak/AndroidAPS)


![BTC](https://bitit.io/assets/coins/icon-btc-1e5a37bc0eb730ac83130d7aa859052bd4b53ac3f86f99966627801f7b0410be.svg) 3KawK8aQe48478s6fxJ8Ms6VTWkwjgr9f2

## Dynamic ISF

To access Dynamic ISF, please use the Dynamic ISF branch. 

## Warning

Dynamic ISF adjusts your ISF value as glucose levels change. If you are using the techniques described by Bernd Herpichbohm to use AAPS in an **Unannounced Meals** configuration, **Please disable all automations that adjust ISF**. If you don't do this, you are likely to end up with values for ISF that are too low and too much insulin may be delivered.   

**You will probably need to reconfigure all your UAM based automations, so it is safer to disable all of them until you are familiar with Dynamic ISF**

### Pre-installation notes

Please note that to use Dynamic ISF effectively, the AndroidAPS database needs a minimum of five days of data. If you are installing version 3 over the top of 2.8 or on a new phone, it's best to run the standard master branch first for those five days. Dynamic ISF will work without the full set of data in the database, as it uses an estimate of TDD based on the profile basal rates, then whatever the database has once data is populated, but it will be more stable with five days of data.

**Fresh installations of v3 crashing**

As mentioned in the last paragraph, if you are installing v3 over v2.8, or fresh on a new phone, the database will be empty. This creates an issue with the SMB plugin on all versions of AndroidAPS, where attempting to use SMB causes the app to crash and restart. To resolve this, you will need to run on AMA for a few hours so that SMB has some data to use for Autosens. SMB will work once this is populated, however, as recommended above, for best results, the database should be populated with five days of TDD data. 

### Changes compared to standard AndroidAPS

Dynamic ISF uses Chris WIlson's model to determine ISF instead of a static profile settings. This is applied only in the openAPSSMB plugin. The openAPSAMA plugin continues to use profile settings for ISF.

The equation implemented is: ISF = 277700 / ( BG * TDD )

The implementation here splits the use into two. One to calculate current ISF for use with predictions, the other to calculate future ISF for use with dosing. 

**Current ISF: predictions**

This uses a combination of the 7 day average TDD and a linear extrapolation of the pumps current total insulin delivered to calculate a TDD for today.

The total daily dose used in the above equation is generally weighted 40% to the 7 day average and 60% to the extrapolation from the pump data. There are some special cases where this isn't applied. These are:

* *When the time is earlier than 5am and the pump TDD is greater than 7 day TDD*
* *When the time is earlier than 7am and the pump TDD is less than 80% of 7 day TDD*

In both these cases, a value of 80% of TDD is used in the caluclation.

*When the pump TDD is less than 33% of 7 day TDD*

In this case, the weighting is changed to be 75% pump TDD, 25% 7 day average TDD.

The dynamic ISF value is still adjusted when setting an activity or eating soon temp target, as per the standard oref model.

**Future ISF: dosing**

Future ISF uses the same TDD value as generated above. It then uses different glucose values dependent on the case:

If delta is positive or zero, or delta is negative but predicted glucose is above target, the future ISF value used for determining insulin required is the same as the current ISF.

If delta is negative, and the predicted glucose level is below target, then the eventual BG value is used to determine ISF, as this is will result in a less aggressive ISF value.
