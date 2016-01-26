Third Party Library Baseline
============================

This package represents a defective third party library.

It contains a few small design flaws on purpose,
which can be fixed by the individual patching technology.

All methods also set a defined system property, so that a test can assert
the patched version didn't set the property (i.e. the patch was applied).