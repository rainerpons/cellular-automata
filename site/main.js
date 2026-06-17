fetch('release.json')
  .then(function (r) { return r.json(); })
  .then(function (data) {
    document.getElementById('version').textContent = data.version || 'Unknown';
    document.getElementById('commit').textContent = data.commit || 'Unknown';
  })
  .catch(function () {
    document.getElementById('version').textContent = 'Unavailable';
    document.getElementById('commit').textContent = 'Unavailable';
  });
