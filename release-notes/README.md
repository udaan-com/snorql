# Release Notes

### Deciding your release version:
- The version present inside `<version></version>` tags in your `pom.xml` will be considered as the release version.<br>
- The version in the pom should be of the format `x.y.z-SNAPSHOT`, where <br> 
  `x = major-version`,<br>
  `y = minor-version`,<br>
  `z = patch-version`<br>
  Increment either or all of the 3 versions based on the type of changes present in the release.


### How to publish a release:
Once you have decided on your version, follow the following steps to publish a release:
- Add a `<version-name>.md` file under `release-notes` directory. This will be used as the content for GitHub release.
  For ex: if your `pom.xml` looks like this: `<version>0.4.22-SNAPSHOT</version>`, your release file will should be named `v0.4.22.md`.<br><br>

- Do an empty commit from `main` branch with the commit message `git commit --allow-empty -m "#release"`
  <i>(Make sure you have the permission)</i><br>
    Considering current pom version to be `0.4.22-SNAPSHOT`,
    - `git commit --allow-empty -m "#release"` would:
        - Publish a maven release with version `0.4.22`
        - Publish a GitHub Release with version `v0.4.22`
        - Increment pom to `0.4.23-SNAPSHOT` to set up for next version<br><br>
    