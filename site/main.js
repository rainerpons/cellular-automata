function createMetadataElement(metadata) {
  if (!metadata || !metadata.value) return null;
  
  const p = document.createElement('p');
  p.textContent = metadata.label + ': ';
  
  const el = document.createElement(metadata.elementType);
  el.id = metadata.id;
  if (metadata.isLink) {
    el.href = metadata.value;
    el.textContent = 'Download';
  } else {
    el.textContent = metadata.value;
  }
  
  p.appendChild(el);
  return p;
}

function appendMetadataElement(container, metadata) {
  const element = createMetadataElement(metadata);
  if (!element) return;

  container.appendChild(element);
}

fetch('release.json')
  .then(function (r) { return r.json(); })
  .then(function (data) {
    const container = document.getElementById('release-metadata');
    if (!container || !data) return;

    console.log('Loaded release metadata:', data);

    const appName = data.app && data.app.name ? data.app.name : null;
    const version = data.release && data.release.version ? data.release.version : null;
    const commit = data.release && data.release.commit ? data.release.commit : null;
    const downloads = data.downloads || {};

    const elementsToRender = [
      { label: 'App', id: 'app-name', value: appName, elementType: 'span' },
      { label: 'Version', id: 'version', value: version, elementType: 'span' },
      { label: 'Commit', id: 'commit', value: commit, elementType: 'code' },
      { label: 'macOS', id: 'download-macos', value: downloads.macos, elementType: 'a', isLink: true },
      { label: 'Windows', id: 'download-windows', value: downloads.windows, elementType: 'a', isLink: true },
      { label: 'Linux', id: 'download-linux', value: downloads.linux, elementType: 'a', isLink: true }
    ];

    for (let i = 0; i < elementsToRender.length; i++) {
      appendMetadataElement(container, elementsToRender[i]);
    }
  })
  .catch(function () {
    // If release.json fails to load, do not render anything.
  });
