document.addEventListener('DOMContentLoaded', async () => {
  const osList = ['macos', 'windows', 'linux'];

  let response;
  try {
    response = await fetch('release.json', { cache: 'no-store' });
  } catch (error) {
    console.warn('Could not fetch release metadata:', error);
    return;
  }

  if (!response.ok) {
    return;
  }

  let data;
  try {
    data = await response.json();
  } catch (error) {
    console.warn('Could not parse release metadata:', error);
    return;
  }

  if (!data?.release?.version || !data?.downloads) {
    return;
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
});
