fs = require 'fs'
{print} = require 'util'
{spawn, exec} = require 'child_process'
which = require('which').sync

files = [
  'bin'
  'src'
]

task 'build', 'Single Build', ->
  options = ['-c', '-b', '-o']
  options = options.concat files
  launch 'coffee', options

task 'watch', 'Continuous Watch & Build', -> 
  options = ['-c', '-b', '-o']
  options = options.concat files
  options.unshift '-w'
  launch 'coffee', options

launch = (cmd, options=[]) ->
  cmd = which(cmd) if which
  app = spawn cmd, options
  app.stdout.pipe(process.stdout)
  app.stderr.pipe(process.stderr)
  app.on 'exit', (status) -> "Exited with status " + status
