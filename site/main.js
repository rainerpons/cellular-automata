document.addEventListener('DOMContentLoaded', () => {
  const osList = ['macos', 'windows', 'linux'];
  
  fetch('release.json', { cache: 'no-store' })
    .then(response => {
      if (!response.ok) {
        throw new Error(`Failed to fetch release metadata: ${response.status}`);
      }
      return response.json();
    })
    .then(data => {
      if (!data || !data.release || !data.downloads) {
        throw new Error('Malformed release metadata');
      }

      if (data.app && data.app.name) {
        const appNameEl = document.getElementById('app-name');
        if (appNameEl) {
          appNameEl.textContent = data.app.name.toUpperCase();
        }
      }

      const version = data.release.version;

      osList.forEach(os => {
        const btn = document.getElementById(`btn-${os}`);
        const versionSpan = document.getElementById(`version-${os}`);
        const downloadUrl = data.downloads[os];

        if (btn && versionSpan && downloadUrl) {
          btn.href = downloadUrl;
          versionSpan.textContent = version;
          versionSpan.removeAttribute('hidden');
        }
      });
    })
    .catch(error => {
      // Gracefully degrade: console log the error but do not break the page.
      // Buttons will keep their fallback GitHub Releases links and version labels will remain hidden.
      console.warn('Could not load release metadata:', error.message);
    });
});
