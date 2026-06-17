function createMetadataElement(metadata) {
  if (!metadata || !metadata.value) return null;
  
  var p = document.createElement('p');
  p.textContent = metadata.label + ': ';
  
  var el = document.createElement(metadata.elementType);
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
  var element = createMetadataElement(metadata);
  if (!element) return;

  container.appendChild(element);
}

fetch('release.json')
  .then(function (r) { return r.json(); })
  .then(function (data) {
    var container = document.getElementById('release-metadata');
    if (!container || !data) return;

    // Handle nested metadata with fallback to old flat structure
    var version = data.release && data.release.version ? data.release.version : data.version;
    var commit = data.release && data.release.commit ? data.release.commit : data.commit;
    var appName = data.app && data.app.name ? data.app.name : null;

    console.log('Loaded release metadata:', { app: appName, version: version, commit: commit, downloads: data.downloads });

    if (appName) {
      appendMetadataElement(container, {
        label: 'App',
        id: 'app-name',
        value: appName,
        elementType: 'span'
      });
    }

    if (version) {
      appendMetadataElement(container, {
        label: 'Version',
        id: 'version',
        value: version,
        elementType: 'span'
      });
    }

    if (commit) {
      appendMetadataElement(container, {
        label: 'Commit',
        id: 'commit',
        value: commit,
        elementType: 'code'
      });
    }

    if (data.downloads) {
      if (data.downloads.macos) {
        appendMetadataElement(container, {
          label: 'macOS',
          id: 'download-macos',
          value: data.downloads.macos,
          elementType: 'a',
          isLink: true
        });
      }
      if (data.downloads.windows) {
        appendMetadataElement(container, {
          label: 'Windows',
          id: 'download-windows',
          value: data.downloads.windows,
          elementType: 'a',
          isLink: true
        });
      }
      if (data.downloads.linux) {
        appendMetadataElement(container, {
          label: 'Linux',
          id: 'download-linux',
          value: data.downloads.linux,
          elementType: 'a',
          isLink: true
        });
      }
    }
  })
  .catch(function () {
    // If release.json fails to load, do not render anything.
  });
