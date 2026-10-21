import ts from 'typescript';
import { mkdirSync,readdirSync,readFileSync,writeFileSync } from 'node:fs';
mkdirSync('.test-build',{recursive:true});
writeFileSync('.test-build/package.json','{"type":"module"}');
for(const file of readdirSync('src/app/core').filter(f=>f.endsWith('.ts'))){
  const input=readFileSync('src/app/core/'+file,'utf8');
  let output=ts.transpileModule(input,{compilerOptions:{target:ts.ScriptTarget.ES2022,module:ts.ModuleKind.ESNext,experimentalDecorators:true,useDefineForClassFields:false}}).outputText;
  output=output.replace(/from '(\.\/[^']+)'/g,"from '$1.js'");
  writeFileSync('.test-build/'+file.replace('.ts','.js'),output);
}
