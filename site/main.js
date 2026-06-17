document.addEventListener('DOMContentLoaded', async () => {
  const osList = ['macos', 'windows', 'linux'];

  try {
    const response = await fetch('release.json', { cache: 'no-store' });
    if (!response.ok) {
      throw new Error(`Failed to fetch release metadata: ${response.status}`);
    }

    const data = await response.json();

    if (!data?.release?.version || !data?.downloads) {
      throw new Error('Malformed release metadata: Missing required properties');
    }

    const appName = data.app?.name || 'Cellular Automata';
    const appNameElements = document.querySelectorAll('.app-name-label');
    appNameElements.forEach(el => {
      el.textContent = appName;
    });

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

  } catch (error) {
    // Gracefully degrade: buttons keep fallback GitHub Releases links,
    // version labels remain hidden, and the page doesn't break.
    console.warn('Could not load release metadata:', error.message);
  }
});
