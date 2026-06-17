function createMetadataElement(metadata) {
  if (!metadata || !metadata.value) return null;
  
  var p = document.createElement('p');
  p.textContent = metadata.label + ': ';
  
  var el = document.createElement(metadata.elementType);
  el.id = metadata.id;
  el.textContent = metadata.value;
  
  p.appendChild(el);
  return p;
}

function appendMetadataElement(container, metadata) {
  var element = createMetadataElement(metadata);
  if (!element) return;

  container.appendChild(element);
}

fetch('release.json')
  .then(function (r) { return r.json(); })
  .then(function (data) {
    var container = document.getElementById('release-metadata');
    if (!container || !data) return;

    appendMetadataElement(container, {
      label: 'Version',
      id: 'version',
      value: data.version,
      elementType: 'span'
    });

    appendMetadataElement(container, {
      label: 'Commit',
      id: 'commit',
      value: data.commit,
      elementType: 'code'
    });
  })
  .catch(function () {
    // If release.json fails to load, do not render anything.
  });
