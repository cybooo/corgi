# CorgiBot Contribution guide
This is a guide on what to do and not to do if you want to contribute to this project. 

### What to do and what to avoid
 - Don't create PR with only one line changed.
 - Always (if possible) test out if everything works.
 - Don't change code that works, if not needed.
 - Check if the PR does not contain any extra files, eg Eclipse files.

### Formatting
Corgi's source is formatted according to the IntelliJ basic format, so we do not accept any other type.
 - The minimum space between the class header and methods is 1 line.

### Branches
Breakdown of how the branches work here:
 - `master` - is the base branch with which Corgi is updated to the major version.
 - `develop` - is a testing version that includes bugs, fixes and pull requests.

### Pull Requests and their types
Before sending PR, check the following things:
 - PR is complete, so it contains all corrections, changes and other adjustments.
 - You tried everything, and everything worked as it should.

Next, make sure your branch meets Corgi's standards:
  - `feature/` - New updates, changes, etc.
  - `fix/` - Bugfixes, the ID of the issue is often written after the /
