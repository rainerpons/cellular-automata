function createVersionElement(version) {
  if (!version) return null;
  
  var p = document.createElement('p');
  p.textContent = 'Version: ';
  
  var span = document.createElement('span');
  span.id = 'version';
  span.textContent = version;
  
  p.appendChild(span);
  return p;
}

function createCommitElement(commit) {
  if (!commit) return null;
  
  var p = document.createElement('p');
  p.textContent = 'Commit: ';
  
  var code = document.createElement('code');
  code.id = 'commit';
  code.textContent = commit;
  
  p.appendChild(code);
  return p;
}

fetch('release.json')
  .then(function (r) { return r.json(); })
  .then(function (data) {
    var container = document.getElementById('release-metadata');
    if (!container || !data) return;

    var versionEl = createVersionElement(data.version);
    if (versionEl) {
      container.appendChild(versionEl);
    }

    var commitEl = createCommitElement(data.commit);
    if (commitEl) {
      container.appendChild(commitEl);
    }
  })
  .catch(function () {
    // If release.json fails to load, do not render anything.
  });
