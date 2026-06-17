function createMetadataElement(label, id, value, elementType) {
  if (!value) return null;
  
  var p = document.createElement('p');
  p.textContent = label + ': ';
  
  var el = document.createElement(elementType);
  el.id = id;
  el.textContent = value;
  
  p.appendChild(el);
  return p;
}

fetch('release.json')
  .then(function (r) { return r.json(); })
  .then(function (data) {
    var container = document.getElementById('release-metadata');
    if (!container || !data) return;

    var versionEl = createMetadataElement('Version', 'version', data.version, 'span');
    if (versionEl) {
      container.appendChild(versionEl);
    }

    var commitEl = createMetadataElement('Commit', 'commit', data.commit, 'code');
    if (commitEl) {
      container.appendChild(commitEl);
    }
  })
  .catch(function () {
    // If release.json fails to load, do not render anything.
  });
