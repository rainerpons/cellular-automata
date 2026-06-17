fetch('release.json')
  .then(function (r) { return r.json(); })
  .then(function (data) {
    var container = document.getElementById('release-metadata');
    if (!container) return;

    if (data.version) {
      var versionP = document.createElement('p');
      versionP.textContent = 'Version: ';
      var versionSpan = document.createElement('span');
      versionSpan.id = 'version';
      versionSpan.textContent = data.version;
      versionP.appendChild(versionSpan);
      container.appendChild(versionP);
    }

    if (data.commit) {
      var commitP = document.createElement('p');
      commitP.textContent = 'Commit: ';
      var commitCode = document.createElement('code');
      commitCode.id = 'commit';
      commitCode.textContent = data.commit;
      commitP.appendChild(commitCode);
      container.appendChild(commitP);
    }
  })
  .catch(function () {
    // If release.json fails to load, do not render anything.
  });
